import MockAdapter from 'axios-mock-adapter';
import { interlocutorsApi } from '@/axios/InterlocutorApis';
import {
  getGateProjectsByDepartment,
  getRequiredActionById,
  sendAction,
} from '@/axios/InterlocutorApis';
import { ActionDto, RequiredActionDto } from '@/types/interlocutor';

describe('interlocutor API', () => {
  let mock: MockAdapter;

  beforeEach(() => {
    mock = new MockAdapter(interlocutorsApi);
  });

  afterEach(() => {
    mock.reset();
  });

  it('gets gate projects by department', async () => {
    const department = 'IT';
    const mockData = [{ id: 1, name: 'Gate A' }];
    mock
      .onGet(`departement-gateproject/gates-affected-department/${department}`)
      .reply(200, mockData);

    const data = await getGateProjectsByDepartment(department);
    expect(data).toEqual(mockData);
  });

  it('gets required action by ID', async () => {
    const id = 42;
    const mockData: RequiredActionDto = {
      id,
      requiredAction: 'Review Document',
      departementGateProjectId: 5,
    };

    mock.onGet(`/required-action/find/${id}`).reply(200, mockData);

    const data = await getRequiredActionById(id);
    expect(data).toEqual(mockData);
  });

  it('throws error if required action by ID not found', async () => {
    const id = 999;
    mock.onGet(`/required-action/find/${id}`).reply(200, null);

    await expect(getRequiredActionById(id)).rejects.toThrow(
      `RequiredAction with id ${id} not found`
    );
  });

  it('sends action with document', async () => {
    const actionDto: ActionDto = {
      actionLabel: 'Submit Action',
      comments: ['Reviewed', 'Accepted'],
      requiredActionId: 10,
    };

    const fakeFile = new File(['dummy content'], 'report.pdf', {
      type: 'application/pdf',
    });

    const mockResponse = { ...actionDto, id: 1 };

    mock.onPost('/action/add').reply(200, mockResponse);

    const result = await sendAction(actionDto, fakeFile);
    expect(result).toEqual(mockResponse);
  });
});
