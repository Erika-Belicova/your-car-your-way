import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../../features/user/services/user.service';
import { catchError, map, of } from 'rxjs';

export const roleGuard: CanActivateFn = (route) => {
  const userService = inject(UserService);
  const router = inject(Router);

  // get the required role from the route data
  const requiredRole = route.data['role'];

  return userService.getCurrentUser().pipe(
    map(user => {
      if (user.role === requiredRole) {
        return true;
      }
      // redirect to 404 if user does not have the required role
      return router.createUrlTree(['/404']);
    }),
    catchError(() => of(router.createUrlTree(['/login'])))
  );
};