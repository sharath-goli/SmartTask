export type Quadrant='DO_FIRST'|'SCHEDULE'|'DELEGATE'|'REDUCE';
export type Task={id:number;title:string;description?:string;status:'TODO'|'IN_PROGRESS'|'DONE';priority:'LOW'|'MEDIUM'|'HIGH';dueDate?:string;estimateMinutes?:number;important:boolean;urgent:boolean;quadrant:Quadrant;tags:string[];recurrence:'NONE'|'DAILY'|'WEEKLY'|'MONTHLY';parentId?:number;createdAt:string;completedAt?:string};
export type TaskInput=Omit<Task,'id'|'quadrant'|'createdAt'|'completedAt'|'tags'>&{tags:string};
const base=(import.meta.env.VITE_API_URL||'http://localhost:8080/api').replace(/\/$/,'');

export class ApiError extends Error {
  constructor(message:string,readonly kind:'network'|'http',readonly status?:number){super(message);this.name='ApiError'}
}

export function classifyQuadrant(important:boolean,urgent:boolean):Quadrant{
  if(important)return urgent?'DO_FIRST':'SCHEDULE';
  return urgent?'DELEGATE':'REDUCE';
}

export async function api<T>(path:string,opts:RequestInit={}):Promise<T>{
  const token=localStorage.getItem('smarttask.token');
  let res:Response;
  try{
    res=await fetch(`${base}${path.startsWith('/')?path:`/${path}`}`,{
      ...opts,
      headers:{'Content-Type':'application/json',...(token?{Authorization:`Bearer ${token}`} :{}),...opts.headers}
    });
  }catch(error){
    if(error instanceof DOMException&&error.name==='AbortError')throw error;
    throw new ApiError(`Cannot reach the SmartTask API at ${base}. Check that the backend is running and VITE_API_URL points to it.`,'network');
  }
  let raw='';
  try{if(res.status!==204)raw=await res.text()}catch{
    throw new ApiError('The connection to the SmartTask API ended before its response was received. Retry the request and check the backend logs.','network');
  }
  let payload:unknown;
  if(raw){try{payload=JSON.parse(raw)}catch{payload=undefined}}
  if(!res.ok){
    const detail=payload&&typeof payload==='object'&&'message'in payload&&typeof payload.message==='string'?payload.message:undefined;
    const message=res.status===401?'Your session has expired or is no longer valid. Sign out, then sign in again.':
      res.status===403?'You do not have permission to do that.':
      detail|| (res.status>=500?'The SmartTask server could not complete the request. Check the backend logs.':`The request failed (${res.status}).`);
    throw new ApiError(message,'http',res.status);
  }
  if(res.status===204)return undefined as T;
  if(raw&&!payload)throw new ApiError('The SmartTask server returned an unreadable response. Check the backend logs.','http',res.status);
  return payload as T;
}
