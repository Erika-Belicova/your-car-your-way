import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-dashboard',
  imports: [],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.scss',
})
export class UserDashboard {

  constructor(private router: Router) {}

  // navigate to support conversation history
  goToConversations(): void {
    this.router.navigate(['/support-conversations']);
  }

  // navigate to new support conversation form
  goToNewConversation(): void {
    this.router.navigate(['/support-conversations/new']);
  }

}