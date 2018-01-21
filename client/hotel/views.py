from django.shortcuts import render
from django.shortcuts import redirect
import urllib.request
import requests
from .forms import OrderForm, OrderModifyForm, BillForm, AuthForm
import datetime
import base64
from django.http import HttpResponse
from django.template import loader
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
    if not 'username' in request.COOKIES or not 'password' in request.COOKIES or not 'access_token' in request.COOKIES or \
        not 'refresh_token' in request.COOKIES or not 'scope' in request.COOKIES:
        return redirect('/hotel/auth')
    username = request.COOKIES['username']
    scope = request.COOKIES['scope']
    token = request.COOKIES['access_token']
    url = 'http://localhost:1212/hotel/user/' + username + '/orders'
    header = {'authorization': 'Bearer ' + token, 'scope': scope}
    try:
        response = requests.get(url, headers=header)
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
    if not 'username' in request.COOKIES or not 'password' in request.COOKIES or not 'access_token' in request.COOKIES or \
        not 'refresh_token' in request.COOKIES or not 'scope' in request.COOKIES:
        return redirect('/hotel/auth')
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
                username = request.COOKIES['username']
                scope = request.COOKIES['scope']
                token = request.COOKIES['access_token']
                url = 'http://localhost:1212/hotel/user/' + username + '/order'
                header = {'authorization': 'Bearer ' + token, 'scope': scope}
                headers = {'content-type': 'application/json'}
                headers.update(header)
                try:
                    print("AMA HERE")
                    print(url)
                    print(headers)
                    response = requests.post(url, data=string, headers=headers)
                    print("CODE = ", response.status_code)
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
    if not 'username' in request.COOKIES or not 'password' in request.COOKIES or not 'access_token' in request.COOKIES or \
        not 'refresh_token' in request.COOKIES or not 'scope' in request.COOKIES:
        return redirect('/hotel/auth')
    username = request.COOKIES['username']
    scope = request.COOKIES['scope']
    token = request.COOKIES['access_token']
    url = 'http://localhost:1212/hotel/user/' + username + '/order/' + order_id
    header = {'authorization': 'Bearer ' + token, 'scope': scope}
    try:
        response = requests.get(url, headers=header)
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
                    temp_header = {'content-type': 'application/json'}
                    temp_header.update(header)
                    response = requests.put('http://localhost:1212/hotel/user/' + username + '/order/' + order_id, 
                        data=string, headers=temp_header)
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
    if not 'username' in request.COOKIES or not 'password' in request.COOKIES or not 'access_token' in request.COOKIES or \
        not 'refresh_token' in request.COOKIES or not 'scope' in request.COOKIES:
        return redirect('/hotel/auth')
    if request.method == 'POST':
        form = BillForm(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            orderId = data['orderId']
            cartNumber = data['cartNumber']
            username = request.COOKIES['username']
            scope = request.COOKIES['scope']
            token = request.COOKIES['access_token']
            if cartNumber <= 0 or orderId <= 0:
                return render(request, 'hotel/bill.html', {'error':'Номер бронирования и номер карты не могут быть отрицательны', 'form':form})
            header = {'authorization': 'Bearer ' + token, 'scope': scope}
            url = 'http://localhost:1212/hotel/user/' + username + '/order/' + str(orderId) + '/billing'
            string = '{\"cartNumber\":' + str(cartNumber) + '}'
            try:
                temp_header = {'content-type': 'application/json'}
                temp_header.update(header)
                response = requests.post(url, data=string, headers=temp_header)
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
    if not 'username' in request.COOKIES or not 'password' in request.COOKIES or not 'access_token' in request.COOKIES or \
        not 'refresh_token' in request.COOKIES or not 'scope' in request.COOKIES:
        return redirect('/hotel/auth')
    username = request.COOKIES['username']
    scope = request.COOKIES['scope']
    token = request.COOKIES['access_token']
    url = 'http://localhost:1212/hotel/user/' + username + '/order/' + order_id
    form = OrderForm()
    header = {'authorization': 'Bearer ' + token, 'scope': scope}
    try:
        response = requests.delete(url, headers=header)
    except Exception:
        context = {'form':form, 'error':'Сервис недоступен'}
        return render(request, 'hotel/order_descrition.html', context)
    if response.status_code == 400:
        context = {'form':form, 'error':'Невозможно удалить бронирование, так как дата заезда раньше текущей даты'}
        return render(request, 'hotel/order_descrition.html', context)
    return redirect('/hotel/order/')

def auth_user(request):
    if request.method == 'POST':
        form = AuthForm(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            v_password = data['password']
            v_username = data['username']
            scope = base64.b64encode(b'client:ui-secret')
            header = {"authorization": "Basic " + scope.decode('utf-8'), "password": v_password, "username": v_username, 'secret':scope}
            # url = "http://localhost:8081/oauth/token?grant_type=password&redirect_uri=https://www.yandex.ru&username=" + v_username + "&password=" + v_password
            url = 'http://localhost:1212/hotel/token'
            try:
                response = requests.post(url, headers=header)
                print("CODE = ", response.status_code)
            except Exception as ecx:
                template = loader.get_template('hotel/auth.html')
                context = {'mainerror':'EXEPTION Сервис недоступен'}
                main_response = HttpResponse(template.render(context, request))
                main_response.delete_cookie('password')
                main_response.delete_cookie('username')
                return main_response
            print(response.status_code)
            if response.status_code == 401:
                template = loader.get_template('hotel/auth.html')
                context = {'error':'Неверный логин и пароль'}
                main_response = HttpResponse(template.render(context, request))
                main_response.delete_cookie('password')
                main_response.delete_cookie('username')
                return main_response
            if response.status_code != 200:
                template = loader.get_template('hotel/auth.html')
                context = {'error':'Ошибка аворизации'}
                main_response = HttpResponse(template.render(context, request))
                main_response.delete_cookie('password')
                main_response.delete_cookie('username')
                return main_response
            template = loader.get_template('hotel/auth.html')
            context = {'error':'good', 'form':form}
            main_response = HttpResponse(template.render(context, request))
            main_response.set_cookie('password', v_password)
            main_response.set_cookie('username', v_username)
            expires = datetime.datetime.strftime(datetime.datetime.utcnow() + datetime.timedelta(seconds=response.json()['expires_in']), "%a, %d-%b-%Y %H:%M:%S GMT")
            main_response.set_cookie('access_token', response.json()['access_token'], expires=expires, max_age=response.json()['expires_in'])
            main_response.set_cookie('refresh_token', response.json()['refresh_token'])
            main_response.set_cookie('scope', response.json()['scope'])
            return main_response
    else:
        form = AuthForm()
    return render(request, 'hotel/auth.html', {'form':form})