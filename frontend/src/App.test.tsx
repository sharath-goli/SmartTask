// @vitest-environment jsdom
import {afterEach,expect,it,vi} from 'vitest';
import {cleanup,fireEvent,render,screen,waitFor} from '@testing-library/react';
import App from './App';

afterEach(()=>{cleanup();localStorage.clear();vi.restoreAllMocks()});

it('renders the sign-in page when signed out',()=>{
  render(<App/>);
  expect(screen.getByRole('heading',{name:'Welcome back'})).toBeTruthy();
  expect(screen.getByRole('button',{name:/Sign in/})).toBeTruthy();
});

it('keeps the authenticated interface visible when the backend is unavailable',async()=>{
  localStorage.setItem('smarttask.token','existing-token');
  localStorage.setItem('smarttask.user',JSON.stringify({id:1,name:'Ari',email:'ari@example.com'}));
  vi.spyOn(globalThis,'fetch').mockRejectedValue(new Error('Backend unavailable'));

  render(<App/>);
  expect(await screen.findByText(/Cannot reach the SmartTask API/)).toBeTruthy();
  expect(screen.getByText(/Here’s what’s happening with your work today/)).toBeTruthy();
});

it('shows a live matrix quadrant and sends urgency and importance when creating a task',async()=>{
  localStorage.setItem('smarttask.token','sample-token');
  localStorage.setItem('smarttask.user',JSON.stringify({id:1,name:'Ari',email:'ari@example.com'}));
  const savedTask={id:12,title:'Prepare presentation',description:'',status:'TODO',priority:'MEDIUM',important:true,urgent:true,quadrant:'DO_FIRST',tags:[],recurrence:'NONE',createdAt:'2026-10-11T00:00:00Z'};
  let persistedTasks:typeof savedTask[]=[];
  const dashboard={total:0,open:0,completed:0,overdue:0,dueToday:0,completionRate:0,trend:[],upcoming:[],recent:[]};
  const fetchMock=vi.spyOn(globalThis,'fetch').mockImplementation(async(input,init)=>{
    const url=String(input);
    if(url.endsWith('/tasks')&&init?.method==='POST'){persistedTasks=[savedTask];return new Response(JSON.stringify(savedTask),{status:200})}
    if(url.endsWith('/tasks'))return new Response(JSON.stringify(persistedTasks),{status:200});
    if(url.endsWith('/dashboard'))return new Response(JSON.stringify(dashboard),{status:200});
    throw new Error(`Unexpected request ${url}`);
  });

  render(<App/>);
  fireEvent.click(screen.getByRole('button',{name:/My tasks/}));
  fireEvent.click(screen.getByRole('button',{name:/New task/}));
  fireEvent.change(screen.getByLabelText('Importance'),{target:{value:'true'}});
  expect(screen.getByRole('status').textContent).toContain('Q2 · Schedule');
  fireEvent.change(screen.getByLabelText('Urgency'),{target:{value:'true'}});
  expect(screen.getByRole('status').textContent).toContain('Q1 · Do First');
  fireEvent.change(screen.getByLabelText('Task title'),{target:{value:'Prepare presentation'}});
  fireEvent.click(screen.getByRole('button',{name:/Create task/}));

  await waitFor(()=>expect(fetchMock.mock.calls.some(([url,init])=>String(url).endsWith('/tasks')&&init?.method==='POST')).toBe(true));
  const createCall=fetchMock.mock.calls.find(([url,init])=>String(url).endsWith('/tasks')&&init?.method==='POST');
  expect(JSON.parse(String(createCall?.[1]?.body))).toMatchObject({important:true,urgent:true,title:'Prepare presentation'});
  await waitFor(()=>expect(screen.queryByRole('heading',{name:'Create a task'})).toBeNull());
  fireEvent.click(screen.getByRole('button',{name:/Eisenhower matrix/}));
  expect(await screen.findByRole('button',{name:/Prepare presentation/})).toBeTruthy();
});

it('returns to sign-in after registration and supports login after logout',async()=>{
  const user={id:3,name:'Ari',email:'ari@example.com'};
  const dashboard={total:0,open:0,completed:0,overdue:0,dueToday:0,completionRate:0,trend:[],upcoming:[],recent:[]};
  const fetchMock=vi.spyOn(globalThis,'fetch').mockImplementation(async(input)=>{
    const url=String(input);
    if(url.endsWith('/auth/register')||url.endsWith('/auth/login'))return new Response(JSON.stringify({token:'new-token',user}),{status:200});
    if(url.endsWith('/tasks'))return new Response('[]',{status:200});
    if(url.endsWith('/dashboard'))return new Response(JSON.stringify(dashboard),{status:200});
    throw new Error(`Unexpected request ${url}`);
  });
  render(<App/>);
  fireEvent.click(screen.getByRole('button',{name:'Create an account'}));
  fireEvent.change(screen.getByLabelText('Your name'),{target:{value:'Ari'}});
  fireEvent.change(screen.getByLabelText('Email address'),{target:{value:'ari@example.com'}});
  fireEvent.change(screen.getByLabelText('Password'),{target:{value:'password-123'}});
  fireEvent.click(screen.getByRole('button',{name:/Create account/}));
  await screen.findByRole('heading',{name:/Good .*Ari/});

  fireEvent.click(screen.getByRole('button',{name:/Settings/}));
  fireEvent.click(screen.getByRole('button',{name:/Sign out/}));
  expect(await screen.findByRole('heading',{name:'Welcome back'})).toBeTruthy();
  fireEvent.change(screen.getByLabelText('Email address'),{target:{value:'ari@example.com'}});
  fireEvent.change(screen.getByLabelText('Password'),{target:{value:'password-123'}});
  fireEvent.click(screen.getByRole('button',{name:/Sign in/}));
  await screen.findByRole('heading',{name:/Good .*Ari/});
  expect(fetchMock.mock.calls.filter(([url])=>String(url).endsWith('/auth/register'))).toHaveLength(1);
  expect(fetchMock.mock.calls.filter(([url])=>String(url).endsWith('/auth/login'))).toHaveLength(1);
});
