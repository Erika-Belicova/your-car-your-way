import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../features/auth/services/auth.service';
import { UserService } from '../../../features/user/services/user.service';
import { UserResponseDTO } from '../../../features/user/interfaces/user-response-dto';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar implements OnInit {

  currentUser: UserResponseDTO | null = null;

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // fetch current user to display email and role in navbar
    this.userService.getCurrentUser().subscribe({
      next: user => this.currentUser = user
    });
  }

  // check if current user is a support agent for role-based styling
  isSupportAgent(): boolean {
    return this.currentUser?.role === 'ROLE_SUPPORT_AGENT';
  }

  // log out current user and redirect to login
  logout(): void {
    this.authService.logout();
  }

}