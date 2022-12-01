from django.db import models
import datetime

# Create your models here.
class Notification(models.Model):
    area_id = models.IntegerField(default = 1)
    pub_date = models.DateTimeField('date published', default = datetime.datetime.now)
    image = models.ImageField(blank=True, null=True)