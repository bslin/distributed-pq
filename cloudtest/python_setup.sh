#!/bin/bash
sudo apt-get install -y gcc
sudo apt-get install -y python3-all-dev
sudo apt-get install -y python3-venv
cd ~/cs244b/final_project/
python3 -m venv venv
source venv/bin/activate
pip install wheel
easy_install gevent
pip install -r simulation/requirements.txt
deactivate
