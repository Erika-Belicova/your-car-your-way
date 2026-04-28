import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from '../../../core/services/token.service';
import { UserService } from '../../../features/user/services/user.service';

@Component({
  selector: 'app-not-found',
  imports: [],
  templateUrl: './not-found.html',
  styleUrl: './not-found.scss',
})
export class NotFound {

  constructor(
    private router: Router,
    private tokenService: TokenService,
    private userService: UserService
  ) {}

  // navigate to main page based on authentication and role
  navigateHome(): void {
    if (!this.tokenService.getAccessToken()) {
      this.router.navigate(['/login']);
      return;
    }
    this.userService.getCurrentUser().subscribe({
      next: user => {
        if (user.role === 'ROLE_SUPPORT_AGENT') {
          // navigate to support agent main page
          this.router.navigate(['/agent-access/support-conversations']);
        } else {
          // navigate to user main page
          this.router.navigate(['/dashboard']);
        }
      },
      // navigate to login page
      error: () => this.router.navigate(['/login'])
    });
  }

}