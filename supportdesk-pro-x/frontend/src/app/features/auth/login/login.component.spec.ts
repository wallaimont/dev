import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';

describe('LoginComponent', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideNoopAnimations(),
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve navegar para root quando login for bem-sucedido', () => {
    authService.login.and.returnValue(
      of({
        accessToken: 'a',
        refreshToken: 'r',
        expiresIn: 900,
        user: { id: '1', fullName: 'User', email: 'user@sdpx.com' }
      })
    );

    component.form.setValue({ email: 'user@sdpx.com', password: '12345678' });
    component.submit();

    expect(authService.login).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('deve exibir erro amigavel quando login falhar', () => {
    authService.login.and.returnValue(throwError(() => ({ error: { message: 'Credenciais invalidas' } })));

    component.form.setValue({ email: 'user@sdpx.com', password: '12345678' });
    component.submit();

    expect(component.error).toBe('Credenciais invalidas');
    expect(component.loading).toBeFalse();
  });
});
