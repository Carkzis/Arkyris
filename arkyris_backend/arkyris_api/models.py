from django.db import models

# Create your models here.

class Entry(models.Model):
    user = models.CharField("Name", max_length=240)
    colour = models.IntegerField()
    date_time = models.DateField(auto_now_add=True)

    def __str__(self):
        return f'User: {self.user} - Date: {self.date_time}'