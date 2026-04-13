import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('calls tasks endpoint with status filter', () => {
    service.list('PENDING').subscribe((response) => {
      expect(response.success).toBeTrue();
      expect(response.data.length).toBe(1);
    });

    const req = httpMock.expectOne('/api/v1/tasks?status=PENDING');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: [{ id: '1', type: 'APPROVAL', status: 'PENDING', timeoutAt: null }] });
  });
});
