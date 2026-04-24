import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';
import { UserService } from '../../features/user/services/user.service';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = () => {

  const tokenService = inject(TokenService);
  const userService = inject(UserService);
  const router = inject(Router);

  // if no access token is found, redirect to login
  if (!tokenService.getAccessToken()) {
    return router.createUrlTree(['/login']);
  }

  // verify if token is valid by calling the API
  return userService.getCurrentUser().pipe(
    map(() => true),
    catchError(() => of(router.createUrlTree(['/login'])))
  );
  
};