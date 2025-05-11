'use client';
import { activityFormSchema } from '@/validation/activityFormSchema';
import { z } from 'zod';
import { ErrorMessage, Field, Form, Formik, FormikHelpers } from 'formik';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { CircleAlert } from 'lucide-react';
import { Input } from '../ui/input';
import { Button } from '../ui/button';
import { Textarea } from '../ui/textarea';
import { Group } from '@/validation/groupSchema';
import { formatDate } from '@/lib/utils';

type ActivityFormValues = z.infer<typeof activityFormSchema>;

interface ActivityFormProps {
  onSubmit: (
    values: ActivityFormValues,
    actions: FormikHelpers<ActivityFormValues>,
  ) => void;
  trip: Group | null;
  initialValues?: ActivityFormValues;
  isSubmitting?: boolean;
}

const defaultInitialValues: ActivityFormValues = {
  name: '',
  description: '',
  location: '',
  link: '',
};

export default function ActivityForm({
  onSubmit,
  trip,
  initialValues = defaultInitialValues,
  isSubmitting = false,
}: ActivityFormProps) {
  return (
    <>
      {trip && (
        <h1 className="text-heading4-semibold">
          {trip.name}

          {trip.tripStartDate && trip.tripEndDate && (
            <>
              {' | '}
              {formatDate(trip.tripStartDate)} - {formatDate(trip.tripEndDate)}
            </>
          )}
        </h1>
      )}
      <Formik
        initialValues={initialValues}
        validationSchema={toFormikValidationSchema(activityFormSchema)}
        onSubmit={(values, actions) => {
          onSubmit(values, actions);
        }}
      >
        {({ errors, touched }) => (
          <Form>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="name">
                Name
              </label>
              <Field
                name="name"
                as={Input}
                className={`mt-1 ${
                  errors.name && touched.name ? 'border-red-500' : ''
                }`}
              />
              <ErrorMessage
                name="name"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="description">
                Description
              </label>
              <Field
                name="description"
                as={Textarea}
                className={`mt-1 ${
                  errors.description && touched.description
                    ? 'border-red-500'
                    : ''
                }`}
              />
              <ErrorMessage
                name="description"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="location">
                Location
              </label>
              <Field
                name="location"
                as={Input}
                className={`mt-1 ${
                  errors.location && touched.location ? 'border-red-500' : ''
                }`}
              />
              <ErrorMessage
                name="description"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="link">
                Link
              </label>
              <Field
                name="link"
                type="url"
                as={Input}
                className={`mt-1 ${
                  errors.link && touched.link ? 'border-red-500' : ''
                }`}
              />
              <ErrorMessage
                name="link"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <Button
              className="bg-primary-600 hover:bg-primary-500 px-5 py-4 mt-4 text-sm"
              type="submit"
              disabled={isSubmitting}
            >
              {isSubmitting
                ? 'Submitting...'
                : initialValues.name
                  ? 'Save Changes'
                  : 'Create an Activity'}
            </Button>
          </Form>
        )}
      </Formik>
    </>
  );
}
