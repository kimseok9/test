"""myserver URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include
from django.conf.urls.static import static
from django.conf import settings
from common.views import redirect_login_page

urlpatterns = [
    path("admin/", admin.site.urls),
    path('common/', include('common.urls')),               # common 에서 설정한 urls 앞에 common/ 이 추가됨
    path('main/', include('main.urls')),                   # main 에서 설정한 urls 앞에 main/ 이 추가됨
    path('api/', include('api.urls')),                     # api 에서 설정한 urls 앞에 api/ 이 추가됨
    path('', redirect_login_page)                          # common/views.py 에서 만든 redirect 함수를 통해, 기본 페이지로 이동 시, 로그인 페이지로 이동하도록 설정
 
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
