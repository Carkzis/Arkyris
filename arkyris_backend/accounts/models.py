from django.db import models

from django.dispatch import receiver
from django.urls import reverse
from django_rest_passwordreset.signals import reset_password_token_created
from django.core.mail import send_mail  

@receiver(reset_password_token_created)
def password_reset_token_created(sender, instance, reset_password_token, *args, **kwargs):

    email_plaintext_message = \
        "{}?token={}".format(reverse('password_reset:reset-password-request'),
        reset_password_token.key)
        
    send_mail(
        'Password reset for the Arkyris app!',
        email_plaintext_message,
        'arkyris@gmail.com', # this isn't a thing
        [reset_password_token.user.email]
    )