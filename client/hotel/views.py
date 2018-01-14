from django.shortcuts import render
from django.shortcuts import redirect
import urllib.request
import requests
from .forms import OrderForm, OrderModifyForm, BillForm
import datetime


def get_message(code):
    if code == 503:
        return 'Сервис недоступен'
    if code == 500:
        return 'Внутреняя ошибка сервера'
    if code == 400:
        return 'Некорректный запрос'
    if code == 404:
        return 'Несуществующий ресурс'


def show_rooms(request, page, size):
    url = 'http://localhost:1212/hotel/rooms?page=' + page + '&size=' + size
    response = None
    try:
        response = requests.get(url)
    except Exception:
        context = {'error':'Сервис недоступен'}
        return render(request, 'hotel/rooms_list.html', context)
    if response.status_code != requests.codes.ok:
        context = {'error':get_message(response.status_code)}
    else:
        rooms = response.json()[0]['content']
        rooms.sort(key=lambda d: d['id'])
        context = {'rooms':rooms}
    return render(request, 'hotel/rooms_list.html', context)

def index_page(request):
    url = 'http://localhost:1212/hotel/roomtypes?page=0&size=10'
    response = None
    try:
        response = requests.get(url)
    except Exception:
        context = {'error':'Сервис недоступен'}
        return render(request, 'hotel/index.html', context)
    if response.status_code != requests.codes.ok:
        context = {'error':get_message(response.status_code)}
    else:
        rooms = response.json()[0]['content']
        context = {'rooms':rooms}
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
    try:
        response = requests.get(url)
    except Exception:
        context = {'error':'Сервис недоступен'}
        return render(request, 'hotel/order.html', context)
    if response.status_code != requests.codes.ok:
        context = {'error':get_message(response.status_code)}
    else:
        orders = response.json()
        orders.sort(key=lambda d: d['id'])
        context = {'orders':orders}
    return render(request, 'hotel/order.html', context)

def make_order(request):
    if request.method == 'POST':
        form = OrderForm(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            error = None
            if data['roomTypeId'] < 0 or data['nightAmount'] <= 0:
                error = 'Тип комнаты и количесво ночей должны принимать значения больше 0'
            if data['arrivalDate'] <= datetime.date.today():
                error = 'Дата прибытия должна быть позже текущей'
            if error is None:
                day = str(data['arrivalDate'].day)
                month = str(data['arrivalDate'].month)
                if len(day) == 1:
                    day = "0" + day
                if len(month) == 1:
                    month = "0" + month
                arrivalDate = str(data['arrivalDate'].year) + ":" + month + ":" + day + "/00:00:00"
                data['arrivalDate'] = arrivalDate
                string = '{\"nightAmount\":' +str(data['nightAmount']) + ', \"arrivalDate\":\"' + arrivalDate + '\", \"roomTypeId\":' + str(data['roomTypeId'])+'}'
                url = 'http://localhost:1212/hotel/user/1/order'
                headers = {'content-type': 'application/json'}
                try:
                    response = requests.post(url, data=string, headers=headers)
                except Exception:
                    context = {'form':form, 'error':'Сервис недоступен'}
                if response.status_code != requests.codes.created:
                    return render(request, 'hotel/add_order.html', {'form':form, 'error':get_message(response.status_code)})
                else:
                    return redirect('/hotel/order')
            else:
                return render(request, 'hotel/add_order.html', {'form':form, 'error':error})
    else:
        form = OrderForm()
    return render(request, 'hotel/add_order.html', {'form':form})


def show_order(request, order_id):
    url = 'http://localhost:1212/hotel/user/1/order/' + order_id
    try:
        response = requests.get(url)
    except Exception:
        return render(request, 'hotel/order_descrition.html', {'mainerror':'Сервис недоступен'})
    if response.status_code != requests.codes.ok:
        return render(request, 'hotel/order_descrition.html', {'mainerror':get_message(response.status_code)})
    order = response.json()
    if request.method == 'POST':
        form = OrderModifyForm(request.POST)
        if form.is_valid():
            error = None
            data = form.cleaned_data
            if data['arrivalDate'] <= datetime.date.today():
                error = 'Дата прибытия должна быть позже текущей'
            if data['nightAmount'] <= 0:
                error = 'Количество ночей должно быть больше 0'
            if error is None:
                day = str(data['arrivalDate'].day)
                month = str(data['arrivalDate'].month)
                if len(day) == 1:
                    day = "0" + day
                if len(month) == 1:
                    month = "0" + month
                arrivalDate = str(data['arrivalDate'].year) + ":" + month + ":" + day + "/00:00:00"
                string = '{\"nightAmount\":' +str(data['nightAmount']) + ', \"arrivalDate\":\"' + arrivalDate + '\"' + '}'
                try:
                    response = requests.put('http://localhost:1212/hotel/user/1/order/' + order_id, data=string, headers={'content-type': 'application/json'})
                except Exception:
                    return render(request, 'hotel/order_descrition.html', {'mainerror':'Сервис недоступен'})
                if response.status_code != requests.codes.ok:
                    return render(request, 'hotel/order_descrition.html', {'error':get_message(response.status_code), 'order':order, 'form':form})
                return redirect('/hotel/order/')
            else:
                context = {'order':order, 'form':form, 'error':error}
                return render(request, 'hotel/order_descrition.html', context)
        else:
            form = OrderModifyForm()
            context = {'order':order, 'form':form, 'error':'Введите корректные данные'}
            return render(request, 'hotel/order_descrition.html', context)
    else:
        form = OrderModifyForm()
    context = {'order':order, 'form':form}
    return render(request, 'hotel/order_descrition.html', context)

def bill_order(request):
    if request.method == 'POST':
        form = BillForm(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            orderId = data['orderId']
            cartNumber = data['cartNumber']
            if cartNumber <= 0 or orderId <= 0:
                return render(request, 'hotel/bill.html', {'error':'Номер бронирования и номер карты не могут быть отрицательны', 'form':form})
            url = 'http://localhost:1212/hotel/user/1/order/' + str(orderId) + '/billing'
            string = '{\"cartNumber\":' + str(cartNumber) + '}'
            try:
                response = requests.post(url, data=string, headers={'content-type': 'application/json'})
            except Exception:
                return render(request, 'hotel/bill.html', {'mainerror':'Сервис недоступен'})
            print("code = ", response.status_code)
            if response.status_code == 406:
                return render(request, 'hotel/bill.html', {'error':'Бронирование уже оплаченно', 'form':form})
            if response.status_code == 404:
                return render(request, 'hotel/bill.html', {'error':'Брони с таким номером не существует', 'form':form})
            if response.status_code == 400:
                return render(request, 'hotel/bill.html', {'error':'Невозможно оплатить заказ с прошедшей датой прибытия', 'form':form})
            if response.status_code != requests.codes.created:
                return render(request, 'hotel/bill.html', {'error':get_message(response.status_code), 'form':form})
            return render(request, 'hotel/bill.html', {'error':'good', 'form':form})
    else:
        form = BillForm()
    return render(request, 'hotel/bill.html', {'form':form})

def delete_order(request, order_id):
    url = 'http://localhost:1212/hotel/user/1/order/' + order_id
    form = OrderForm()
    try:
        response = requests.delete(url)
    except Exception:
        context = {'form':form, 'error':'Сервис недоступен'}
        return render(request, 'hotel/order_descrition.html', context)
    if response.status_code == 400:
        context = {'form':form, 'error':'Невозможно удалить бронирование, так как дата заезда раньше текущей даты'}
        return render(request, 'hotel/order_descrition.html', context)
    return redirect('/hotel/order/')


