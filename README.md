# distributed-pq
Distributed priority queue for CS244b class project

## Python:
1. Creating a Python VM:
   * Linux/Mac

        ```bash
        python3 -m venv venv
        source venv/bin/activate
        pip install -r simulation/requirements.txt
        ```

   * Windows:

        ```bat
        python3 -m venv venv
        .\venv\Scripts\activate
        pip install -r simulation\requirements.txt
        ```

2. To update requirements:

    ```bash
    pip freeze > simulation/requirements.txt
    ```
