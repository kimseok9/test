from django.shortcuts import render,redirect, get_object_or_404
from django.http import JsonResponse, HttpResponseNotAllowed
from django.views.decorators.csrf import csrf_exempt
from django.db.models import Q, Count
from django.core.paginator import Paginator
from .models import *
from rest_framework.authtoken.models import Token
from django.contrib import messages



# 기본 render 함수
def base_request(request):
    return render(request, 'base.html')

# 녹음파일 서버에 저장하는 함수
@csrf_exempt
def download_sound_file(request):
    if request.method == 'POST':
        dong = request.POST['dong']
        ho = request.POST['ho']
        file_name = request.POST['file_name']
        sound_file = request.FILES['sound_file']
        model = models.Sound_File(dong=dong, ho=ho, file_name=file_name, sound_file=sound_file)
        model.save()

        print('Downloaded sound file:', dong, ho, file_name, sound_file)
        msg = {'result': 'success'}

    else:
        msg = {'result': 'fail'}

    return JsonResponse(msg)

