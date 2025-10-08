import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { RestService } from './rest.service';
import { OEM, OemResponseBody, SelectedOEM } from '../models/oem.model';
import { getUrlPathFragment } from './_static/util';
import { ServerResponseWithBody } from '../models/app.models';

@Injectable({
  providedIn: 'root'
})
export class OemService {
  private selectedOEMSubject = new BehaviorSubject<string | null>(null);
  selectedOEM$ = this.selectedOEMSubject.asObservable();

  constructor(private restService: RestService) {}

  setSelectedOEM(oem: SelectedOEM) {
    sessionStorage.setItem('selectedOEM', JSON.stringify(oem));
    this.selectedOEMSubject.next(JSON.stringify(oem));
  }

  getSelectedOEM(): string | null {
    return sessionStorage.getItem('selectedOEM');
  }

  // API Methods using getUrlPathFragment
  getAllOEMs(): Observable<ServerResponseWithBody<OemResponseBody>> {
    return this.restService.read<ServerResponseWithBody<OemResponseBody>>(
      getUrlPathFragment('oems', 'available'))
      
  }

  validateOEMAccess(oemId: string): Observable<ServerResponseWithBody<any>> {
    return this.restService.read<ServerResponseWithBody<any>>(
      getUrlPathFragment('oems', oemId, 'config')
    );
  }

  getOEMDetails(oemId: string): Observable<ServerResponseWithBody<OEM>> {
    return this.restService.read<ServerResponseWithBody<OEM>>(
      getUrlPathFragment('oems', oemId)
    );
  }

}