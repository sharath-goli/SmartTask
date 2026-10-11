// @vitest-environment jsdom
import {afterEach,describe,expect,it,vi} from 'vitest';
import {api,classifyQuadrant} from './api';

afterEach(()=>{localStorage.clear();vi.restoreAllMocks()});

describe('api client',()=>{
  it('sends the saved bearer token and decodes JSON',async()=>{
    localStorage.setItem('smarttask.token','sample-token');
    const fetchMock=vi.spyOn(globalThis,'fetch').mockResolvedValue(new Response('{"ok":true}',{status:200}));
    await expect(api<{ok:boolean}>('/tasks')).resolves.toEqual({ok:true});
    expect(fetchMock.mock.calls[0][0]).toContain('/tasks');
    expect((fetchMock.mock.calls[0][1]?.headers as Record<string,string>).Authorization).toBe('Bearer sample-token');
  });

  it('surfaces API validation messages',async()=>{
    vi.spyOn(globalThis,'fetch').mockResolvedValue(new Response('{"message":"title: must not be blank"}',{status:400}));
    await expect(api('/tasks')).rejects.toThrow('title: must not be blank');
  });

  it('distinguishes a backend network failure from an API validation response',async()=>{
    vi.spyOn(globalThis,'fetch').mockRejectedValue(new TypeError('Failed to fetch'));
    await expect(api('/auth/login')).rejects.toThrow(/Cannot reach the SmartTask API/);
  });

  it.each([
    [true,true,'DO_FIRST'],[true,false,'SCHEDULE'],[false,true,'DELEGATE'],[false,false,'REDUCE']
  ] as const)('classifies importance=%s urgency=%s as %s', (important,urgent,expected)=>{
    expect(classifyQuadrant(important,urgent)).toBe(expected);
  });
});
