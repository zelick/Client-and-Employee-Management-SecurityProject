import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/env/environment';
import { User } from '../model/user.model';
import { Observable } from 'rxjs';
import { RegistrationRequestResponse } from '../model/registrationRequestResponse.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly apiUrl = `${environment.apiHost}`;

  constructor(private http: HttpClient) {}

  registerUser(user : User): Observable<String> {
    return this.http.post<String>(this.apiUrl + 'users/registerUser', user);
  }

  getAllRegistrationRequests(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl + 'users/getAllRegistrationRequests');
  }

  processRegistrationRequest(responseData: RegistrationRequestResponse): Observable<User[]> {
    return this.http.put<User[]>(this.apiUrl + 'users/processRegistrationRequest', responseData);
  }

}
