import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from '../services/token.service';
import { AuthHttpService } from '../services/auth-http.service';
import { UserService } from '../../features/user/services/user.service';
import { catchError, switchMap, throwError } from 'rxjs';

const excludedEndpoints = ['/api/auth/login', '/api/auth/refresh'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const tokenService = inject(TokenService);
  const authHttpService = inject(AuthHttpService);
  const userService = inject(UserService);
  const router = inject(Router);

  const accessToken = tokenService.getAccessToken();
  const url = new URL(req.url, window.location.origin);

  // clear tokens, cached user and redirect to login
  const clearAndRedirect = () => {
    tokenService.clearTokens();
    userService.clearCache();
    router.navigate(['/login']);
  };

  // attach access token to request if it exists and endpoint is not excluded
  let request = req;
  if (accessToken && !excludedEndpoints.includes(url.pathname)) {
    request = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${accessToken}`)
    });
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !excludedEndpoints.includes(url.pathname)) {
        const refreshToken = tokenService.getRefreshToken();

        if (!refreshToken) {
          // no refresh token available, clear tokens and redirect to login
          clearAndRedirect();
          return throwError(() => error);
        }

        // attempt to refresh the access token
        return authHttpService.refresh(refreshToken).pipe(
          switchMap(response => {
            // save new access token and retry request
            tokenService.saveAccessToken(response.accessToken);
            const retryRequest = req.clone({
              headers: req.headers.set('Authorization', `Bearer ${response.accessToken}`)
            });
            return next(retryRequest);
          }),
          catchError(refreshError => {
            // refresh failed, clear tokens and redirect to login
            clearAndRedirect();
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
  
};