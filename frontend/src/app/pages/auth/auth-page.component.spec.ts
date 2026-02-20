import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthPageComponent } from './auth-page.component';
import { AuthService } from '../../core/auth.service';
import { Router } from '@angular/router';

describe('AuthPageComponent', () => {
  let component: AuthPageComponent;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login']);
    authService.login.and.returnValue(of({ userId: 1, email: 'user@example.com' }));

    TestBed.configureTestingModule({
      imports: [AuthPageComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: jasmine.createSpyObj<Router>('Router', ['navigate']) }
      ]
    });

    component = TestBed.createComponent(AuthPageComponent).componentInstance;
  });

  it('requires minimum password length of 4 characters', () => {
    component.form.setValue({ email: 'user@example.com', password: '123' });

    expect(component.form.invalid).toBeTrue();
    expect(component.form.controls.password.hasError('minlength')).toBeTrue();
  });

  it('does not submit when password is too short', () => {
    component.form.setValue({ email: 'user@example.com', password: '123' });
    component.submit();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('submits when password has 4+ characters', () => {
    component.form.setValue({ email: 'user@example.com', password: '1234' });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith('user@example.com', '1234');
  });
});
