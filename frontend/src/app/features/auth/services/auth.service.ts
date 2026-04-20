import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { TokenService } from '../../../core/services/token.service';
import { UserService } from '../../user/services/user.service';
import { AuthResponse } from '../../../shared/interfaces/auth-response';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private userService: UserService,
    private router: Router
  ) {}

  // authenticate user and store access and refresh tokens
  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { email, password }).pipe(
      tap(response => {
        this.tokenService.saveAccessToken(response.accessToken);
        this.tokenService.saveRefreshToken(response.refreshToken);
      })
    );
  }

  // clear tokens and cached user data and redirect to login
  logout(): void {
    this.tokenService.clearTokens();
    this.userService.clearCache();
    this.router.navigate(['/login']);
  }

}