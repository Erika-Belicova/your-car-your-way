import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ConversationService } from '../../../../core/services/conversation.service';

@Component({
  selector: 'app-user-conversation-form',
  imports: [ReactiveFormsModule],
  templateUrl: './user-conversation-form.html',
  styleUrl: './user-conversation-form.scss',
})
export class UserConversationForm {

  conversationForm: FormGroup;
  submitted = false;
  errorMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private conversationService: ConversationService,
    private router: Router
  ) {
    // form with subject and initial message validation
    this.conversationForm = this.fb.group({
      subject: ['', [Validators.required, Validators.maxLength(255)]],
      messageContent: ['', [Validators.required]]
    });
  }

  // submit form and navigate to conversation detail on success
  onSubmit(): void {
    this.submitted = true;
    if (this.conversationForm.invalid) {
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';

    this.conversationService.createConversation(this.conversationForm.value).subscribe({
      next: conversation => {
        this.isLoading = false;
        // navigate to the created conversation detail page
        this.router.navigate(['/support-conversations', conversation.id]);
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Failed to create conversation. Please try again.';
      }
    });
  }

  // navigate back to dashboard
  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

}
