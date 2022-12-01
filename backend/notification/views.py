from datetime import timezone
from pathlib import Path
from django.shortcuts import render

def index(request):
    return render(request, "notification/index.html")

def area(request, area_name):
    return render(request, "notification/notification.html", {"area_name": area_name})
