import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthPageComponent } from './auth-page.component';
import { AuthService } from '../../core/auth.service';
import { Router } from '@angular/router';

describe('AuthPageComponent', () => {
  let component: AuthPageComponent;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login', 'register']);
    authService.login.and.returnValue(of({ token: 'token', userId: 1, email: 'user@example.com' }));
    authService.register.and.returnValue(of({ token: 'token', userId: 1, email: 'user@example.com' }));

    TestBed.configureTestingModule({
      imports: [AuthPageComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: jasmine.createSpyObj<Router>('Router', ['navigate']) }
      ]
    });

    component = TestBed.createComponent(AuthPageComponent).componentInstance;
  });

  it('requires minimum password length of 8 characters', () => {
    component.form.setValue({ email: 'user@example.com', password: '1234567' });

    expect(component.form.invalid).toBeTrue();
    expect(component.form.controls.password.hasError('minlength')).toBeTrue();
  });

  it('does not submit when password length is 6-7 characters', () => {
    component.form.setValue({ email: 'user@example.com', password: '123456' });
    component.submit();

    expect(authService.login).not.toHaveBeenCalled();

    component.form.setValue({ email: 'user@example.com', password: '1234567' });
    component.submit();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('submits when password has 8+ characters', () => {
    component.form.setValue({ email: 'user@example.com', password: '12345678' });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith('user@example.com', '12345678');
  });
});
