from rest_framework import serializers
from .models import Entry

class EntrySerializer(serializers.ModelSerializer):

    class Meta:
        model = Entry
        fields = ['pk', 'user', 'colour', 'date_time', 'public', 'deleted']