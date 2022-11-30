import datetime
from channels.generic.websocket import AsyncWebsocketConsumer
from channels.db import database_sync_to_async
from channels.layers import get_channel_layer
import json
from asgiref.sync import async_to_sync

from django.db.models.signals import post_save
from django.dispatch import receiver

import os
import sys
sys.path.append("/home/irteam/2022-2-SCS4031-SantongSantong/backend")
import django

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "backend.settings")
django.setup()

from models.models import Notification
from models.serializers import NotificationSerializer


def get_notification():
    notifications = Notification.objects.all()
    serializer = NotificationSerializer(notifications, many=True)
    return serializer


# @receiver(post_save, sender=Notification)
# def send_update(sender, instance, created, **kwargs):
#     print("New reading in DB")
#     serializer = NotificationSerializer(instance)

#     if created:
#         print("New saving in DB")
#         channel_layer = get_channel_layer()
#         async_to_sync(channel_layer.group_send)(
#             "area_name", {"type": "notify", "data": serializer.data}
#         )


class NotificationConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.area_name = self.scope["url_route"]["kwargs"]["area_name"]  # notification/routing.py에 있는 center_name
        self.group_name = 'models_%s' % self.area_name
        
        await self.channel_layer.group_add(  # group 참여
            self.group_name, self.channel_name
        )

        await self.accept()  # websocket 연결

        # notification이 있으면 알람 전송
        notifications = get_notification()
        if notifications:
            await self.channel_layer.group_send(
                "area_name", {
                    "type": "notify",
                    "data": notifications
                }
            )

    async def notify(self, event):
        # Send message to WebSocket
        # notification = get_notification()
        # if notification:
            # await self.send(data = json.dumps({
            #         "type": "notify",
            #         "data": {
            #             "area_id" : 1,
            #             "pub_date" : datetime.datetime.now(),
            #             "img" : "backend\models\static\00000000.jpg",
            #         }
            #     }))
        await self.send(text_data=json.dumps(event))

    async def disconnect(self, close_code):
        # Leave group
        await self.channel_layer.group_discard(
            self.group_name, self.channel_name
        )
    
    # @receiver(post_save, sender=Notification)
    # def send_update(sender, instance, created, **kwargs):
    #     print("New reading in DB")
    #     serializer = NotificationSerializer(instance)

    #     if created:
    #         print("New saving in DB")
    #         channel_layer = get_channel_layer()
    #         async_to_sync(channel_layer.group_send)(
    #             "area_name", {"type": "notify", "data": serializer.data}
    #         )

    