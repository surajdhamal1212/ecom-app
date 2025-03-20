import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';

const BASIC_URL = 'http://localhost:8080/auth';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  // Register user
  register(signupRequest: any): Observable<any> {
    return this.http
      .post(BASIC_URL + '/register', signupRequest, {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
        }),
        withCredentials: true,
      })
      .pipe(
        catchError((error) => {
          console.error('Error during registration:', error);
          return throwError(
            () => new Error('Registration failed. Please try again.')
          );
        })
      );
  }

  // User login
  login(authRequest: any): Observable<any> {
    return this.http.post(BASIC_URL + '/login', authRequest).pipe(
      catchError((error) => {
        return throwError(
          () => new Error('Login failed. Please check your credentials.')
        );
      })
    );
  }

  // Save JWT token to localStorage
  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // Get JWT token from localStorage
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Clear token from localStorage
  clearToken(): void {
    localStorage.removeItem('token');
  }
}

// import { HttpClient } from '@angular/common/http';
// import { Injectable } from '@angular/core';
// import { Observable, catchError, throwError } from 'rxjs';

// const BASIC_URL = 'http://localhost:8080'; // Changed to string

// @Injectable({
//   providedIn: 'root',
// })
// export class AuthService {
//   constructor(private http: HttpClient) {}

//   register(signupRequest: any): Observable<any> {
//     return this.http.post(BASIC_URL + '/sign-up', signupRequest).pipe(
//       catchError((error: any) => {
//         return throwError(
//           () =>
//             new Error(
//               error.error.message || 'Registration failed. Please try again.'
//             )
//         );
//       })
//     );
//   }

//   login(authRequest: any): Observable<any> {
//     return this.http.post(BASIC_URL + '/authenticate', authRequest).pipe(
//       catchError((error) => {
//         return throwError(
//           () => new Error('Login failed. Please check your credentials.')
//         );
//       })
//     );
//   }

//   saveToken(token: string): void {
//     localStorage.setItem('token', token);
//   }

//   getToken(): string | null {
//     return localStorage.getItem('token');
//   }

//   clearToken(): void {
//     localStorage.removeItem('token');
//   }
// }
