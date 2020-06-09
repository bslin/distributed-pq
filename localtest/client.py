import math
import random
import grequests


"""
java -Dserver.port=8080 -jar target/priorityqueue-0.0.1.war 0 2 
java -Dserver.port=8081 -jar target/priorityqueue-0.0.1.war 1 2
java -Dserver.port=8082 -jar target/priorityqueue-0.0.1.war 2 2
"""
class PQClient:

    def __init__(self, num_peeks=2, num_instances_per_host=10, hosts=['localhost'], fault_tolerant=False):
        self.pool = grequests.Pool(num_peeks)
        self.num_peeks = num_peeks
        self.fault_tolerant = fault_tolerant
        self.base_urls = []
        for host in hosts:
            for i in range(num_instances_per_host):
                self.base_urls.append(f'http://{host}:{8080+i}/')

    def get_rand_url(self):
        return self.get_rand_urls(1)[0]

    def get_rand_urls(self, num):
        return random.sample(self.base_urls, num)

    def add(self, element):
        req = grequests.post(self.get_rand_url() + 'add', data={'priority': element, 'message': 'xyz'})
        response = req.send().response
        if not self.fault_tolerant:
            assert response.ok

    def clear(self):
        for url in self.base_urls:
            req = grequests.post(url + 'popAll')
            assert req.send().response.ok
            print(f'clearing {req.url} {req.response.json()}')

    def pop(self):
        if len(self.base_urls) == 0:
            return None

        chosen_nodes = self.get_rand_urls(min(self.num_peeks, len(self.base_urls)))
        reqs = [grequests.post(node + 'peek') for node in chosen_nodes]

        responses = grequests.map(reqs)
        valid_responses = []
        if (self.fault_tolerant):
            for i in range(len(responses)):
                if responses[i] is None or (not responses[i].ok) or len(responses[i].text) == 0:
                    if chosen_nodes[i] in self.base_urls:
                        self.base_urls.remove(chosen_nodes[i])
                else:
                    valid_responses.append(responses[i])
        else:
            valid_responses = responses

        if len(valid_responses) == 0:
            return None

        min_response = min(
            valid_responses,
            key=lambda r: r.json()['key']['priority'] if (r.ok and len(r.text) > 0) else math.inf
        )

        req = grequests.post(min_response.url.replace('peek', 'pop')).send()
        if not self.fault_tolerant:
            assert req.response.ok

        res = req.response.json() if req.response is not None and req.response.ok and len(req.response.text) > 0 else None
        if res is not None:
            grequests.post(min_response.url.replace('peek', 'commit'), data=res['key']).send()
            # assert req.response.ok
        return res


