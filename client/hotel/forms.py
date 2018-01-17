from django import forms
import datetime

class OrderForm(forms.Form):
    roomTypeId = forms.IntegerField(initial=1)
    nightAmount = forms.IntegerField(initial=1)
    arrivalDate = forms.DateField(initial=datetime.date.today)
    # fields = ('roomTypeId', 'nightAmount', 'arrivalDate')

class OrderModifyForm(forms.Form):
    nightAmount = forms.IntegerField(initial=1)
    arrivalDate = forms.DateField(initial=datetime.date.today)


class BillForm(forms.Form):
    orderId = forms.IntegerField(initial=1)
    cartNumber = forms.IntegerField(initial=12345)


class AuthForm(forms.Form):
    username = forms.CharField(initial='janna')#'Input your username')
    password = forms.CharField(initial='12345')#'Input your password')