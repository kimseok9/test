from django.urls import path
from . import views
from django.views.generic import TemplateView

app_name = 'main'

urlpatterns = [

    path('base/', views.base_request, name='base'),

    # iot 관련
    path('sound_file/', views.download_sound_file, name='sound_file'),
]

# 게시판 html로 이동하는 url 필요 또는, base.html에 게시판 띄우는 작업 필요