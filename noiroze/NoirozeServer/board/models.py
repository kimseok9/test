from django.db import models
from common.models import CustomUser
from django.utils import timezone
from django.conf import settings

class CommunityBoard(models.Model):                                                                       # 커뮤니티 게시판 모델
    CATEGORY_CHOICES = (
        ('정보공유', '정보공유'),
        ('소통해요', '소통해요'),
        ('칭찬해요', '칭찬해요'),
        ('나눔해요', '나눔해요'),
    )
    category = models.CharField('게시판 카테고리', max_length=10, choices=CATEGORY_CHOICES, default='정보공유')    # 카테고리
    title = models.CharField('게시판 제목', max_length=200)                                                       # 제목
    content = models.TextField('게시판 내용')                                                                     # 내용
    author = models.ForeignKey(CustomUser, on_delete=models.CASCADE)                                             # 작성자
    created_date = models.DateTimeField('작성 일시', default=timezone.now)                                        # 작성일시
    modify_date = models.DateTimeField('수정 일시', null=True, blank=True)                                        # 수정일시
    like=models.ManyToManyField(settings.AUTH_USER_MODEL, related_name='like', blank=True)                                   # 추천 수

    def __str__(self):
        return self.title
    


class Reply(models.Model):                                                                                      # 커뮤니티 게시판 댓글 모델
    community_board = models.ForeignKey(CommunityBoard, on_delete=models.CASCADE)                               # 게시판 모델 외래키 설정
    author = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, default=1, null=True)        # 게시판 작성자 외래키 설정
    content = models.TextField('댓글 내용')                                                                                # 댓글 내용
    created_date = models.DateTimeField('작성 일시', default=timezone.now)                                                                # 댓글 생성일
    modify_date = models.DateTimeField('수정 일시', null=True, blank=True)                                                   # 댓글 수정일
    # reply_like = models.ManyToManyField(settings.AUTH_USER_MODEL, related_name='reply_like')

    def __str__(self):
        return self.content
    

class ComplainBoard(models.Model):                            # 민원접수 게시판 모델
    title = models.CharField('민원 제목', max_length=200) 
    content = models.TextField('민원 내용')  
    author = models.ForeignKey(CustomUser, on_delete=models.CASCADE)  
    created_date = models.DateTimeField('작성 일시', default=timezone.now)

    def __str__(self):
        return self.title


class NoticeBoard(models.Model) :
    title = models.CharField(max_length=200)
    content = models.TextField()
    created_date = models.DateTimeField('작성 일시', default=timezone.now)

    def __str__(self):
        return self.title
    
class Answer(models.Model):
    author = models.ForeignKey(CustomUser, on_delete=models.CASCADE)
    question = models.ForeignKey(ComplainBoard, on_delete=models.CASCADE)
    content = models.TextField()
    create_date = models.DateTimeField()
