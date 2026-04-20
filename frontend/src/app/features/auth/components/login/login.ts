import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../../user/services/user.service';
import { TokenService } from '../../../../core/services/token.service';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {

  loginForm: FormGroup;
  submitted = false;
  errorMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private tokenService: TokenService,
    private router: Router
  ) {
    // redirect authenticated users to their main page based on role
    if (this.tokenService.getAccessToken()) {
      this.userService.getCurrentUser().subscribe({
        next: user => {
          if (user.role === 'ROLE_SUPPORT_AGENT') {
            this.router.navigate(['/agent-access/support-conversations']);
          } else {
            this.router.navigate(['/dashboard']);
          }
        }
      });
    }

    // login form with email and password validation
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  // submit login form and redirect based on user role
  onSubmit(): void {
    this.submitted = true;
    if (this.loginForm.invalid) {
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).pipe(
      switchMap(() => this.userService.getCurrentUser())
    ).subscribe({
      next: user => {
        this.isLoading = false;
        // redirect based on user role
        if (user.role === 'ROLE_SUPPORT_AGENT') {
          this.router.navigate(['/agent-access/support-conversations']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Invalid email or password. Please try again.';
      }
    });
  }

}