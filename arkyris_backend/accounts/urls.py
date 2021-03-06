from .views import RegisterAPI, LoginAPI, UserAPI, ChangePasswordView
from django.urls import path, include
from knox import views as knox_views

urlpatterns = [
    path('register/', RegisterAPI.as_view(), name='register'),
    path('login/', LoginAPI.as_view(), name='login'),
    path('logout/', knox_views.LogoutView.as_view(), name='logout'),
    path('logoutall/', knox_views.LogoutAllView.as_view(), name='logoutall'),
    path('change_password/', ChangePasswordView.as_view(), name='change_password'),
    path('user/', UserAPI.as_view(), name='user'),
]