from rest_framework import serializers

class NotificationSerializer(serializers.Serializer):
    area_id = serializers.IntegerField()
    pub_date = serializers.DateTimeField()
    image = serializers.ImageField()