import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/env/environment';
import { User } from '../model/user.model';
import { Observable } from 'rxjs';
import { RegistrationRequestResponse } from '../model/registrationRequestResponse.model';
import { ResponseMessage } from '../model/responseMessage.model';
import { AdRequest } from '../model/adRequest.model';
import { Ad } from '../model/ad.model';

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

  findUserByEmail(): Observable<User> {
    return this.http.get<User>(this.apiUrl + 'users/findUserByEmail');
  }
  
  updateClient(user: User): Observable<any> {
    return this.http.put<any>(this.apiUrl + 'users/updateClient', user, { responseType: 'text' as 'json' });
  }

  createAdRequest(adRequest: AdRequest): Observable<string> {
    return this.http.post<string>(this.apiUrl + 'ad-requests', adRequest, { responseType: 'text' as 'json' });
  }

  getAllAdRequests(): Observable<AdRequest[]> {
    return this.http.get<AdRequest[]>(this.apiUrl + 'ad-requests');
  }

  createAd(ad: Ad): Observable<string> {
    return this.http.post<string>(this.apiUrl + 'ads', ad, { responseType: 'text' as 'json' });
  }

  getAdRequestById(id: number): Observable<AdRequest> {
    return this.http.get<AdRequest>(`${this.apiUrl}ad-requests/${id}`);
  }

}
