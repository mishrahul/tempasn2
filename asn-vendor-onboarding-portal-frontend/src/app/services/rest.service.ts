import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/environments/environment';
import { mergeHeaders } from './_static/util';

@Injectable({
  providedIn: 'root',
})
export class RestService {
  constructor(private http: HttpClient) { }

  read<T>(urlFragment: string, baseUrl?: string, options?: { headers?: HttpHeaders}): Observable<T> {
    const headers = mergeHeaders(options?.headers);
    if (!!baseUrl) {
      return this.http.get<T>(`${baseUrl}${urlFragment}`, { headers });
    } else {
      return this.http.get<T>(`${environment.baseUrl}${urlFragment}`, { headers });
    }
  }

  delete<T>(urlFragment: string, options?: { headers?: HttpHeaders}): Observable<T> {
    const headers = mergeHeaders(options?.headers);
    return this.http.delete<T>(`${environment.baseUrl}${urlFragment}`, { headers });
  }

  deleteWithBody<T>(urlFragment: string, body?: any, options?: { headers?: HttpHeaders}): Observable<T> {
    const httpOptions = {
      headers: mergeHeaders(options?.headers),
      body: body as any // Specify body type
    };

    return this.http.delete<T>(`${environment.baseUrl}${urlFragment}`, httpOptions).pipe(
      map(response => response as T) // Cast response to type T
    );
  }
  
  readBlob(urlFragment: string, options?: { headers?: HttpHeaders}) {
    const headers = mergeHeaders(options?.headers);
    return this.http.get(`${environment.baseUrl}${urlFragment}`, {
      headers: headers,
      responseType: 'blob',
    });
  }

  post<TIn, TOut>(
    urlFragment: string,
    data: TIn,
    baseUrl?: string,
    options?: { headers?: HttpHeaders}
  ): Observable<TOut> {
    let headers = mergeHeaders(options?.headers);
    if (data instanceof FormData) {
      // Let the browser set the Content-Type header for FormData
      headers = headers.delete('Content-Type');
    }
    if (!!baseUrl) {
      return this.http.post<TOut>(`${baseUrl}${urlFragment}`, data, { headers });
    }
    return this.http.post<TOut>(`${environment.baseUrl}${urlFragment}`, data, { headers });
  }

  put<TIn, TOut>(urlFragment: string, data: TIn, options?: { headers?: HttpHeaders}): Observable<TOut> {
    let headers = mergeHeaders(options?.headers);
    if (data instanceof FormData) {
      // Let the browser set the Content-Type header for FormData
      headers = headers.delete('Content-Type');
    }
    return this.http.put<TOut>(`${environment.baseUrl}${urlFragment}`, data, { headers });
  }
}
