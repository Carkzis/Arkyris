from django.urls import include, path
from .views import EntryCreate, EntryList, EntryDetail, EntryUpdate, EntryDelete

urlpatterns = [
    path('create/', EntryCreate.as_view(), name='create-entry'),
    path('', EntryList.as_view()),
    path('<int:pk>/', EntryDetail.as_view(), name='retrieve-entry'),
    path('update/<int:pk>/', EntryUpdate.as_view(), name='update-entry'),
    path('delete/<int:pk>/', EntryDelete.as_view(), name='delete-entry'),
]