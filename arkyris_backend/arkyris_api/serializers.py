from rest_framework import serializers
from .models import Entry

class EntrySerializer(serializers.ModelSerializer):
    """
    Serializer for loading the request body with the diary entry information.
    """
    class Meta:
        model = Entry
        fields = ['pk', 'user', 'colour', 'date_time', 'public', 'deleted']