from django.conf.urls import url
from django.urls import path, re_path

from . import views

urlpatterns = [
    re_path(r'index$', views.index_page, name='index_page'),
    re_path(r'^rooms/page=(?P<page>\d{0,10000})&size=(?P<size>\d{0,10000})$', views.show_rooms, name='show_rooms'),
    re_path(r'^roomtypes/page=(?P<page>\d{0,10000})&size=(?P<size>\d{0,10000})$', views.show_rooms_type, name='show_rooms'),
    re_path(r'^order', views.show_orders, name='show_order'),
]