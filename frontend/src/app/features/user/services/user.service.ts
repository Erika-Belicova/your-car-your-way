import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, shareReplay } from 'rxjs/operators';
import { TokenService } from '../../../core/services/token.service';
import { environment } from '../../../../environments/environment';
import { UserResponseDTO } from '../../user/interfaces/user-response-dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private cachedUser$?: Observable<UserResponseDTO>;

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {}

  // fetch current user from the API and cache the result to avoid repeated calls
  getCurrentUser(): Observable<UserResponseDTO> {
    if (!this.tokenService.getAccessToken()) {
      return throwError(() => new Error('No access token found.'));
    }
    if (!this.cachedUser$) {
      this.cachedUser$ = this.http.get<UserResponseDTO>(`${environment.apiUrl}/user/me`).pipe(
        shareReplay(1),
        catchError(err => {
          this.clearCache();
          return throwError(() => err);
        })
      );
    }
    return this.cachedUser$;
  }

  // clear cached user data
  clearCache(): void {
    this.cachedUser$ = undefined;
  }

}