import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/env/environment';
import { User } from '../model/user.model';
import { Observable } from 'rxjs';
import { RegistrationRequestResponse } from '../model/registrationRequestResponse.model';
import { ResponseMessage } from '../model/responseMessage.model';
import { LoginReponse } from '../model/loginResponse.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly apiUrl = `${environment.apiHost}`;

  constructor(private http: HttpClient) {}

  registerUser(user: User): Observable<ResponseMessage> {
    return this.http.post<ResponseMessage>(this.apiUrl + 'users/registerUser', user);
  }

  getAllRegistrationRequests(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl + 'users/getAllRegistrationRequests');
  }

  processRegistrationRequest(responseData: RegistrationRequestResponse): Observable<User[]> {
    console.log(responseData.email);
    console.log(responseData.accepted);
    console.log(responseData.reason);
    console.log(responseData);
    return this.http.put<User[]>(this.apiUrl + 'users/processRegistrationRequest', responseData);
  }

  getUserData(): Observable<User> {
    return this.http.get<User>(this.apiUrl + 'users/getUserData');
  }

  updateUserData(userData: any): Observable<ResponseMessage> {
    return this.http.put<ResponseMessage>(this.apiUrl + 'users/updateUserData', userData);
  }

  changePassword(passwordData: any): Observable<ResponseMessage> {
    return this.http.put<ResponseMessage>(this.apiUrl + 'users/updatePassword', passwordData);
  }

  getAllEmployees(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl + 'users/getAllEmployees');
  }

  getAllClients(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl + 'users/getAllClients');
  }

  login(email: string, password: string): Observable<LoginReponse> { 
    const loginData = {
      email: email,
      password: password
    };
    return this.http.post<LoginReponse>(this.apiUrl + 'users/login', loginData);
  }
}
