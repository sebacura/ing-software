import requests


def main():
    IP = "192.168.150.2"
    mrz_code = "00001K6JF794032132904250"    
    msg = requests.get(f'http://{IP}:8000/?{mrz_code}')


if __name__ == "__main__":
    main()