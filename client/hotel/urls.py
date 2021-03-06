from django.conf.urls import url
from django.urls import path, re_path

from . import views

urlpatterns = [
    re_path(r'index$', views.index_page, name='index_page'),
    re_path(r'^rooms/page=(?P<page>\d{0,10000})&size=(?P<size>\d{0,10000})$', views.show_rooms, name='show_rooms'),
    re_path(r'^roomtypes/page=(?P<page>\d{0,10000})&size=(?P<size>\d{0,10000})$', views.show_rooms_type, name='show_rooms_type'),
    re_path(r'^order/$', views.show_orders, name='show_orders'),
    re_path(r'^order/create$', views.make_order, name='make_order'),
    re_path(r'^billing/$', views.bill_order, name='make_order'),
    re_path(r'^order/(\d{0,10000})$', views.show_order, name='show_order'),
    re_path(r'^order/(\d{0,10000})/delete$', views.delete_order, name='delete_order'),
    re_path(r'^auth$', views.auth_user, name='auth'),
    re_path(r'^statistic/auth', views.show_statistic_auth, name='statistic/auth'),
    re_path(r'^statistic/user', views.show_statistic_user, name='statistic/user'),
    re_path(r'^statistic/room', views.show_statistic_room, name='statistic/room'),
]