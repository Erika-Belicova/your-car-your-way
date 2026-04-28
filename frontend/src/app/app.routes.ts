import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [

  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // public routes
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/components/login/login')
        .then(m => m.Login)
  },

  // user protected routes
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/user/components/user-dashboard/user-dashboard')
        .then(m => m.UserDashboard),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ROLE_USER' }
  },
  {
    path: 'support-conversations',
    loadComponent: () =>
      import('./features/user/components/user-conversation-list/user-conversation-list')
        .then(m => m.UserConversationList),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ROLE_USER' }
  },
  {
    path: 'support-conversations/new',
    loadComponent: () =>
      import('./features/user/components/user-conversation-form/user-conversation-form')
        .then(m => m.UserConversationForm),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ROLE_USER' }
  },
  {
    path: 'support-conversations/:id',
    loadComponent: () =>
      import('./features/user/components/user-conversation-detail/user-conversation-detail')
        .then(m => m.UserConversationDetail),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ROLE_USER' }
  },

  // support agent protected routes
  {
    path: 'agent-access/support-conversations',
    loadComponent: () =>
      import('./features/support-agent/components/agent-conversation-list/agent-conversation-list')
        .then(m => m.AgentConversationList),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ROLE_SUPPORT_AGENT' }
  },
  {
    path: 'agent-access/support-conversations/:id',
    loadComponent: () =>
      import('./features/support-agent/components/agent-conversation-detail/agent-conversation-detail')
        .then(m => m.AgentConversationDetail),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ROLE_SUPPORT_AGENT' }
  },

  // not found
  {
    path: '404',
    loadComponent: () =>
      import('./shared/components/not-found/not-found')
        .then(m => m.NotFound)
  },
  { path: '**', redirectTo: '/404' }
  
];