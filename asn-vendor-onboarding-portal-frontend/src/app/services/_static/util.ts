import { HttpHeaders } from "@angular/common/http";

export const URL_SEPARATOR = '/';

export function getUrlPathFragment(...fragments: any[]): string {
  let path = '';

  fragments.forEach((f,i) => {
    path += `${f}${i+1 < fragments.length ? URL_SEPARATOR : ''}`;
  });

  return path;
}

// Utility function to merge custom headers with default headers
export const mergeHeaders = (customHeaders?: HttpHeaders): HttpHeaders => {
  let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  
  if (customHeaders) {
    headers = customHeaders.keys().reduce(
      (acc, key) => acc.set(key, customHeaders.get(key) as string), 
      headers
    );
  }

  return headers;
};