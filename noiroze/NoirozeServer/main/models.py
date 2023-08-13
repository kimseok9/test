from django.db import models
# from common.models import CustomUser
# from django.utils import timezone
from django.conf import settings

# Create your models here.

# 사운드 센서 층간소음 측정
class Sound_Level(models.Model):
    DONG_CHOICES = (
        ('101', '101동'),
        ('102', '102동'),
        ('103', '103동'),
        ('104', '104동'),
        ('105', '105동'),
    )
    dong = models.CharField('동', max_length=10, choices=DONG_CHOICES, default='101')
    HO_CHOICES = (
        ('101', '101호'),
        ('102', '102호'),
        ('201', '201호'),
        ('202', '202호'),
        ('301', '301호'),
        ('302', '302호'),
        ('401', '401호'),
        ('402', '402호'),
        ('501', '501호'),
        ('502', '502호'),
        ('601', '601호'),
        ('602', '602호'),
        ('701', '701호'),
        ('702', '702호'),
        ('801', '801호'),
        ('802', '802호'),
        ('901', '901호'),
        ('902', '902호'),
        ('1001', '1001호'),
        ('1002', '1002호'),
    )
    ho = models.CharField('호수', max_length=5, choices=HO_CHOICES, default='101')             # 호수 필드 추가
    PLACE_CHOICES = (
        ('거실', '거실'),
        ('안방', '안방'),
        ('주방', '주방'),
        ('방1', '방1'),
        ('방2', '방2'),
    )
    place = models.CharField('센서 설치 장소', max_length=10, choices=PLACE_CHOICES, default='거실')       # 센서 설치 장소
    value = models.FloatField('데시벨 측정 값')                   # 센서 값 ( dB(A) )
    created_at = models.DateTimeField('측정 시간')           # 측정 날짜-시간



# 층간소음 녹음 파일
class Sound_File(models.Model):
    DONG_CHOICES = (
        ('101', '101동'),
        ('102', '102동'),
        ('103', '103동'),
        ('104', '104동'),
        ('105', '105동'),
    )
    dong = models.CharField('동', max_length=10, choices=DONG_CHOICES, default='101')
    HO_CHOICES = (
        ('101', '101호'),
        ('102', '102호'),
        ('201', '201호'),
        ('202', '202호'),
        ('301', '301호'),
        ('302', '302호'),
        ('401', '401호'),
        ('402', '402호'),
        ('501', '501호'),
        ('502', '502호'),
        ('601', '601호'),
        ('602', '602호'),
        ('701', '701호'),
        ('702', '702호'),
        ('801', '801호'),
        ('802', '802호'),
        ('901', '901호'),
        ('902', '902호'),
        ('1001', '1001호'),
        ('1002', '1002호'),
    )
    ho = models.CharField('호수', max_length=5, choices=HO_CHOICES, default='101')             # 호수 필드 추가
    PLACE_CHOICES = (
        ('거실', '거실'),
        ('안방', '안방'),
        ('주방', '주방'),
        ('방1', '방1'),
        ('방2', '방2'),
    )
    place = models.CharField('소리 녹음 장소', max_length=10, choices=PLACE_CHOICES, default='거실')       # 음성파일 녹음 장소
    value = models.FloatField('데시벨 측정 값', null=True)                       # 녹음 측정 시, 그때의 데시벨 측정값.
    file_name = models.CharField('녹음파일 명', max_length=40)                # 녹음된 파일 명
    sound_file = models.FileField('실제 파일 이름', upload_to="sound_file/%Y_%m_%d", null=True)     # 수신받은 파일이 서버에 저장될 위치
    created_at = models.DateTimeField('녹음파일 생성일', auto_now_add=True)           # 녹음 날짜-시간




# 층간소음 AI 필터링 후 데이터
class Sound_Level_Verified(models.Model):
    DONG_CHOICES = (
        ('101', '101동'),
        ('102', '102동'),
        ('103', '103동'),
        ('104', '104동'),
        ('105', '105동'),
    )
    dong = models.CharField('동', max_length=10, choices=DONG_CHOICES, default='101')
    HO_CHOICES = (
        ('101', '101호'),
        ('102', '102호'),
        ('201', '201호'),
        ('202', '202호'),
        ('301', '301호'),
        ('302', '302호'),
        ('401', '401호'),
        ('402', '402호'),
        ('501', '501호'),
        ('502', '502호'),
        ('601', '601호'),
        ('602', '602호'),
        ('701', '701호'),
        ('702', '702호'),
        ('801', '801호'),
        ('802', '802호'),
        ('901', '901호'),
        ('902', '902호'),
        ('1001', '1001호'),
        ('1002', '1002호'),
    )
    ho = models.CharField('호수', max_length=5, choices=HO_CHOICES, default='101')              # 호수 필드 추가
    PLACE_CHOICES = (
        ('거실', '거실'),
        ('안방', '안방'),
        ('주방', '주방'),
        ('방1', '방1'),
        ('방2', '방2'),
    )
    place = models.CharField('센서 설치 장소', max_length=10, choices=PLACE_CHOICES, default='거실')       # 센서 설치 장소
    value = models.FloatField('데시벨 측정 값', null=True)                   # 센서 값 ( dB(A) )
    created_at = models.DateTimeField('측정 시간', null=True)           # 측정 날짜-시간
    TYPE_CHOICES = (
        ('충격음', '충격음'),
        ('가구끄는소리', '가구끄는소리'),
        ('진공청소기', '진공청소기'),
        ('악기소리', '악기소리'),
        ('반려동물', '반려동물')
    )
    sound_type = models.CharField('소음 종류', max_length=30, choices=TYPE_CHOICES, default='발걸음소리')  
    file_name = models.CharField('녹음 파일 명', max_length=40, null=True)   # 녹음된 파일 명


