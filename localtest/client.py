import math
import random
import grequests


"""
java -Dserver.port=8080 -jar target/priorityqueue-0.0.1.war 0 2 
java -Dserver.port=8081 -jar target/priorityqueue-0.0.1.war 1 2
java -Dserver.port=8082 -jar target/priorityqueue-0.0.1.war 2 2
"""
class PQClient:

    def __init__(self, num_peeks=2, num_instances=10, host='localhost'):
        self.pool = grequests.Pool(num_peeks)
        self.num_peeks = num_peeks
        self.base_urls = []
        for i in range(num_instances):
            self.base_urls.append(f'http://{host}:{8080+i}/')

    def get_rand_url(self):
        return self.get_rand_urls(1)[0]

    def get_rand_urls(self, num):
        return random.sample(self.base_urls, num)

    def add(self, element):
        req = grequests.post(self.get_rand_url() + 'add', data={'priority': element, 'message': 'xyz'})
        assert req.send().response.ok

    def clear(self):
        for url in self.base_urls:
            req = grequests.post(url + 'popAll')
            assert req.send().response.ok
            print(f'clearing {req.url} {req.response.json()}')

    def pop(self):
        reqs = []
        for i in range(self.num_peeks):
            reqs.append(grequests.post(self.get_rand_url() + 'peek'))

        responses = grequests.map(reqs)
        min_response = min(
            responses,
            key=lambda r: r.json()['key']['priority'] if (r.ok and len(r.text) > 0) else math.inf
        )
        req = grequests.post(min_response.url.replace('peek', 'pop')).send()
        assert req.response.ok

        res = req.response.json() if len(req.response.text) > 0 else None
        if res is not None:
            grequests.post(min_response.url.replace('peek', 'commit'), data=res['key']).send()
            assert req.response.ok
        return res


