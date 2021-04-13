from django.shortcuts import render
from .models import Entry
from rest_framework import generics
from .serializers import EntrySerializer

# Create your views here.

class EntryCreate(generics.CreateAPIView):
    # Allows creation of a new entry
    queryset = Entry.objects.all()
    serializer_class = EntrySerializer

class EntryList(generics.ListAPIView):
    # Allows entries to be listed and viewed
    queryset = Entry.objects.all()
    serializer_class = EntrySerializer

class EntryDetail(generics.RetrieveAPIView):
    # Returns a single entry using primary key
    queryset = Entry.objects.all()
    serializer_class = EntrySerializer

class EntryUpdate(generics.RetrieveUpdateAPIView):
    # Allows record to be updated
    queryset = Entry.objects.all()
    serializer_class = EntrySerializer

class EntryDelete(generics.RetrieveDestroyAPIView):
    # Allows entry to be deleted
    queryset = Entry.objects.all()
    serializer_class = EntrySerializer