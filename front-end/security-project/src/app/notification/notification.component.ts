import { Component } from '@angular/core';
import {  Notification } from '../model/notification.model';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent {
  //  notifications: Notification[] = [];
  notifications: Notification[] = [
    {
      id: 1,
      message: 'New order received.',
      createdAt: new Date('2024-06-15T09:00:00Z')
    },
    {
      id: 2,
      message: 'Payment processed successfully.',
      createdAt: new Date('2024-06-15T10:30:00Z')
    },
    {
      id: 3,
      message: 'Shipment dispatched.',
      createdAt: new Date('2024-06-14T15:45:00Z')
    },
    {
      id: 4,
      message: 'Reminder: Webinar starts in 1 hour.',
      createdAt: new Date('2024-06-14T16:00:00Z')
    },
    {
      id: 5,
      message: 'New message from support team.',
      createdAt: new Date('2024-06-13T11:20:00Z')
    }
  ];

   constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.fetchNotifications();
  }

  fetchNotifications(): void {
    this.userService.getAllNotifications().subscribe(
      (notifications: Notification[]) => {
        // Sortiramo notifikacije po vremenu kreiranja (createdAt)
        this.notifications = notifications.sort((a, b) => {
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        });
      },
      (error: any) => {
        console.error('Failed to fetch notifications:', error);
        // Moguće je dodati logiku za prikaz greške korisniku
      }
    );
  }
}
