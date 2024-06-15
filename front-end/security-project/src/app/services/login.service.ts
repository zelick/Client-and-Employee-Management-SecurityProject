import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private readonly apiUrl = `${environment.apiHost}`;

  constructor(private http: HttpClient) {}
  
  sendEmail(email: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}login/send-email`, email);
  }

  resetPassword(email: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}login/reset-password`, email);
  }
}
