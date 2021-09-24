from django.contrib.auth import login
from django.contrib.auth.models import User

from rest_framework import generics, permissions, status
from rest_framework.response import Response
from rest_framework.authtoken.serializers import AuthTokenSerializer
from knox.models import AuthToken
from knox.views import LoginView as KnoxLoginView
from .serializers import UserSerializer, RegisterSerializer, ChangePasswordSerializer
from rest_framework.permissions import IsAuthenticated

class RegisterAPI(generics.GenericAPIView):
    """
    API for registering a new user.
    """
    serializer_class = RegisterSerializer

    def post(self, request, *args, **kwargs):
        # Adds the request to the serializer, validates it, saves it and returns a response.
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        return Response({
            "user": UserSerializer(user, context=self.get_serializer_context()).data,
            "token": AuthToken.objects.create(user)[1]
        })

class LoginAPI(KnoxLoginView):
    """
    API for logging in a user.
    """
    permission_classes = (permissions.AllowAny,)

    def post(self, request, format=None):
        # Adds the request to the serializer, validates it, and posts the request.
        serializer = AuthTokenSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        login(request, user)
        return super(LoginAPI, self).post(request, format=None)

class UserAPI(generics.RetrieveAPIView):
    """
    API for getting user information.
    """
    permission_classes = (permissions.IsAuthenticated,)
    serializer_class = UserSerializer

    def get_object(self):
        # Returns a user object.
        return self.request.user

class ChangePasswordAPI(generics.UpdateAPIView):
    """
    API to allow a user to change their password, assuming they have
    provided the correct old password.
    """
    serializer_class = ChangePasswordSerializer
    model = User
    permissions_classes = (IsAuthenticated,)

    def get_object(self, queryset=None):
        # Returns a user object.
        return self.request.user
    
    def update(self, request, *args, **kwargs):
        # Retrieves and object, and loads request to the serializer.
        self.object = self.get_object()
        serializer = self.get_serializer(data=request.data)

        if serializer.is_valid():
            # If the old password does not match the users current password, return an error.
            if not self.object.check_password(serializer.data.get("old_password")):
                return Response(
                    {"old_password": ["Wrong password."]},
                    status=status.HTTP_400_BAD_REQUEST
                    )
            # Otherwise, set the new password to the user object, and save it.        
            self.object.set_password(serializer.data.get("new_password"))
            self.object.save()
            # Create a success response, and return it.
            response = {
                'status': 'success',
                'code': 'status.HTTP_200_OK',
                'message': 'Password updated successfully.',
                'data': []
            }
            return Response(response)
        
        # If we get here, we gave a bad request, so return an error.
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)