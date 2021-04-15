from django.db import models

# Create your models here.

class Entry(models.Model):
    user = models.CharField("Name", max_length=240)
    colour = models.IntegerField()
    date_time = models.DateTimeField(auto_now_add=True)
    # 0 = private, 1 = public
    public = models.IntegerField() 
    # for soft deletion, 0 = not deleted, 1 = deleted
    deleted = models.IntegerField()

    # Set in reverse time order
    class Meta:
        ordering = ['-date_time', 'user']

    def __str__(self):
        return f'User: {self.user} - Date: {self.date_time}'