from django.shortcuts import render
import urllib.request
import requests
# Create your views here.



def show_rooms(request, page, size):
    url = 'http://localhost:1212/hotel/rooms?page=' + page + '&size=' + size
    print(url)
    response = requests.get(url)
    print('response  ', response)
    rooms = response.json()[0]['content']
    rooms.sort(key=lambda d: d['id'])
    context = {'rooms':rooms}
    return render(request, 'hotel/rooms_list.html', context)

def index_page(request):
    url = 'http://localhost:1212/hotel/roomtypes?page=0&size=10'
    print(url)
    response = requests.get(url)
    print('response  ', response)
    rooms = response.json()[0]['content']
    context = {'rooms':rooms}
    print(rooms)
    return render(request, 'hotel/index.html', context)

def show_rooms_type(request, page, size):
    url = 'http://localhost:1212/hotel/roomtypes?page=' + page + '&size=' + size
    print(url)
    response = requests.get(url)
    print('response  ', response)
    rooms = response.json()[0]['content']
    context = {'rooms':rooms}
    print(rooms)
    return render(request, 'hotel/index.html', context)

def show_orders(request):
    url = 'http://localhost:1212/hotel/user/1/orders'
    print(url)
    response = requests.get(url)
    orders = response.json()#[0]['content']
    context = {'orders':orders}
    print(orders)
    return render(request, 'hotel/order.html', context)